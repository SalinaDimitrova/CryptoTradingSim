import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';  // STOMP.js library for WebSocket communication
import SockJS from 'sockjs-client'; // SockJS client for WebSocket fallback

const WebSocketService = () => {
    const [prices, setPrices] = useState({});
    const [cryptoSymbol, setCryptoSymbol] = useState("BTC/USD");

    useEffect(() => {
        // Create a new WebSocket client using SockJS and STOMP
        const client = new Client({
            brokerURL: 'ws://localhost:8080/crypto-updates',  // Backend WebSocket URL
            connectHeaders: {},
            debug: function (str) {
                console.log(str);  // For debugging WebSocket messages
            },
            onConnect: () => {
                // Once connected, subscribe to price updates for the selected crypto symbol
                const topic = `/topic/price-update/`;  // Dynamic topic based on symbol
                client.subscribe(topic, (message) => {
                    const updatedPrice = JSON.parse(message.body);  // Parse the incoming message
                    console.log("Received price update", updatedPrice)
                    setPrices((prevPrices) => ({
                        ...prevPrices,
                        [cryptoSymbol]: updatedPrice,  // Update price for the selected crypto symbol
                    }));
                });
            },
            onStompError: (frame) => {
                console.error('STOMP error: ' + frame.headers['message']);
            },
        });

        client.activate();  // Start the WebSocket connection

        // Cleanup function to deactivate WebSocket when component unmounts
        return () => {
            client.deactivate();
        };
    }, [cryptoSymbol]);  // Reconnect if the cryptoSymbol changes

    // Handle symbol change (when the user types a new symbol)
    const handleSymbolChange = (event) => {
        setCryptoSymbol(event.target.value);  // Update symbol for the subscription
    };

    return (
        <div>
            <h2>Real-Time Crypto Prices</h2>
            <input 
                type="text" 
                value={cryptoSymbol} 
                onChange={handleSymbolChange} 
                placeholder="Enter crypto symbol (e.g., BTC/USD)"
            />
            <div>
                <h3>Price: {prices[cryptoSymbol] ? `$${prices[cryptoSymbol]}` : 'Loading...'}</h3>
            </div>
        </div>
    );
};

export default WebSocketService;
