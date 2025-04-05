import { useEffect, useState } from 'react';
import axios from 'axios';
import BuySellForm from '../components/BuySellForm';

const Home = () => {
  const [cryptos, setCryptos] = useState([]);
  const [refreshFlag, setRefreshFlag] = useState(false);

  useEffect(() => {
    axios.get('http://localhost:8080/api/top-cryptos')
      .then((response) => setCryptos(response.data))
      .catch((error) => console.error("Error fetching crypto data:", error));
  }, [refreshFlag]);

  const triggerRefresh = () => setRefreshFlag(!refreshFlag);

  return (
    <div className="max-w-7xl mx-auto py-6 px-4">
      <h2 className="text-3xl font-semibold text-center text-gray-900 my-6">Top 20 Cryptocurrencies</h2>

      <div className="overflow-x-auto shadow-lg rounded-lg bg-white mb-8">
        <table className="min-w-full text-sm text-left text-gray-700">
          <thead className="bg-indigo-600 text-white">
            <tr>
              <th className="px-6 py-3">Cryptocurrency</th>
              <th className="px-6 py-3">Price</th>
              <th className="px-6 py-3">Volume (24h)</th>
            </tr>
          </thead>
          <tbody className="bg-gray-50">
            {cryptos.map((crypto, index) => (
              <tr key={index} className="hover:bg-gray-100">
                <td className="px-6 py-4 flex items-center gap-2">
                  <img src={`https://cryptoicons.org/api/icon/${crypto.symbol.toLowerCase()}/32`} alt={crypto.symbol} />
                  {crypto.name} ({crypto.symbol})
                </td>
                <td className="px-6 py-4">${crypto.price.toFixed(2)}</td>
                <td className="px-6 py-4">{crypto.volume.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="bg-white p-6 rounded-lg shadow-lg">
        <BuySellForm onOrderPlaced={triggerRefresh} />
      </div>
    </div>
  );
};

export default Home;