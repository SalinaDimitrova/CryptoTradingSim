import { useState, useEffect } from 'react';
import axios from 'axios';
import { Client } from '@stomp/stompjs'; // Import STOMP client
import SockJS from 'sockjs-client'; // Import SockJS

const BuySellForm = ({ onOrderPlaced }) => {
    const [cryptoSymbol, setCryptoSymbol] = useState('');
    const [quantity, setQuantity] = useState('');
    const [price, setPrice] = useState('');
    const [orderType, setOrderType] = useState('BUY');
    const [status, setStatus] = useState('');
    const [receivedPrice, setReceivedPrice] = useState(null); // State to store the received price

    useEffect(() => {
        // Establish WebSocket connection when the component is mounted
        const socket = new SockJS('http://localhost:8080/ws'); // Replace with your WebSocket URL
        const stompClient = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log('Connected to WebSocket');
                // Subscribe to the price updates topic
                stompClient.subscribe('/topic/prices', (message) => {
                    const updatedPrice = JSON.parse(message.body);
                    setReceivedPrice(updatedPrice);
                    console.log('Received price update:', updatedPrice);
                });
            },
            onStompError: (frame) => {
                console.error('Error connecting to WebSocket:', frame);
            },
        });

        stompClient.activate(); // Start the WebSocket connection

        // Cleanup on component unmount
        return () => {
            stompClient.deactivate();
        };
    }, []);

    const handleSubmit = (event) => {
        event.preventDefault();

        // Construct the order payload
        const orderData = {
            symbol: cryptoSymbol,
            quantity: quantity,
            price: price,
            type: orderType,
        };

        // Send the order data to the backend
        axios.post('/api/orders', orderData)
            .then((response) => {
                setStatus('Order placed successfully!');
                onOrderPlaced(); // Trigger page refresh in parent
            })
            .catch((error) => {
                setStatus('Error placing order. Please try again.');
                console.error('Error placing order:', error);
            });
    };

    return (
        <div>
            <h3 className="text-xl font-semibold text-center my-6">Place an Order</h3>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block">Crypto Symbol:</label>
                    <input
                        type="text"
                        value={cryptoSymbol}
                        onChange={(e) => setCryptoSymbol(e.target.value)}
                        className="border px-4 py-2"
                        required
                    />
                </div>
                <div>
                    <label className="block">Quantity:</label>
                    <input
                        type="number"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        className="border px-4 py-2"
                        required
                    />
                </div>
                <div>
                    <label className="block">Price (USD):</label>
                    <input
                        type="number"
                        value={price}
                        onChange={(e) => setPrice(e.target.value)}
                        className="border px-4 py-2"
                        required
                    />
                </div>
                <div>
                    <label className="block">Order Type:</label>
                    <select
                        value={orderType}
                        onChange={(e) => setOrderType(e.target.value)}
                        className="border px-4 py-2"
                    >
                        <option value="BUY">Buy</option>
                        <option value="SELL">Sell</option>
                    </select>
                </div>
                <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded mt-4">
                    Place Order
                </button>
                {status && <p className="mt-2">{status}</p>}
            </form>

            {/* Display received price updates */}
            {receivedPrice && (
                <div className="mt-4">
                    <h4 className="text-lg font-semibold">Latest Price Update</h4>
                    <p>{receivedPrice.symbol}: {receivedPrice.price} USD</p>
                </div>
            )}
        </div>
    );
};

export default BuySellForm;
