import { useEffect, useState } from 'react';
import axios from 'axios';
import BuySellForm from '../components/BuySellForm';

const Home = () => {
    const [cryptos, setCryptos] = useState([]);
    const [refreshFlag, setRefreshFlag] = useState(false);

    useEffect(() => {
        axios.get('/api/top-cryptos')  // Update API endpoint as needed
            .then((response) => {
                setCryptos(response.data); // Assume response.data is the list of top cryptos
            })
            .catch((error) => {
                console.error("Error fetching crypto data:", error);
            });
    }, [refreshFlag]);

    const triggerRefresh = () => setRefreshFlag(!refreshFlag); // Trigger page refresh after order placement

    return (
        <div>
            <h2 className="text-2xl font-semibold text-center my-6">Top 20 Cryptocurrencies</h2>
            <div className="overflow-x-auto mb-8">
                <table className="min-w-full text-sm text-left">
                    <thead className="bg-gray-200">
                        <tr>
                            <th className="px-4 py-2">Cryptocurrency</th>
                            <th className="px-4 py-2">Price</th>
                            <th className="px-4 py-2">Volume (24h)</th>
                        </tr>
                    </thead>
                    <tbody>
                        {cryptos.map((crypto, index) => (
                            <tr key={index}>
                                <td className="px-4 py-2">{crypto.name} ({crypto.symbol})</td>
                                <td className="px-4 py-2">${crypto.price}</td>
                                <td className="px-4 py-2">{crypto.volume}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Buy/Sell form */}
            <BuySellForm onOrderPlaced={triggerRefresh} />
        </div>
    );
};

export default Home;
