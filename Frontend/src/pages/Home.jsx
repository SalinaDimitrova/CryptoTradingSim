// pages/Home.jsx
import { useEffect, useState } from 'react';
import axios from 'axios';
import BuySellForm from '../components/BuySellForm';

const Home = () => {
  const [cryptos, setCryptos] = useState([]);
  const [selectedCrypto, setSelectedCrypto] = useState(null);
  const [refreshFlag, setRefreshFlag] = useState(false);

  useEffect(() => {
    axios.get('http://localhost:8080/api/top-cryptos')
      .then((res) => setCryptos(res.data))
      .catch((err) => console.error("Failed to fetch cryptos:", err));
  }, [refreshFlag]);

  const triggerRefresh = () => {
    setRefreshFlag(!refreshFlag);
    setSelectedCrypto(null);
  };

  return (
    <div className="max-w-7xl mx-auto py-6 px-4 pt-28">
      <h2 className="text-5xl font-extrabold text-center text-white mb-6 tracking-tight leading-tight">
        Commission-Free Crypto Investing
      </h2>
      <p className="text-gray-400 text-lg mb-10 text-center max-w-2xl mx-auto">
        Start investing with real-time insights and no hidden fees.
      </p>
      <div className="text-center mb-10">
        <button
          onClick={() => document.getElementById('crypto-table')?.scrollIntoView({ behavior: 'smooth' })}
          className="bg-blue-600 hover:bg-blue-700 text-white px-10 py-3 rounded-full text-lg font-semibold transition duration-300 ease-in-out shadow-lg hover:shadow-xl"
        >
          🚀 Start Trading
        </button>
      </div>

      <div id="crypto-table" className="overflow-x-auto shadow-lg rounded-lg bg-white mb-8 text-gray-800">
        <table className="min-w-full text-sm text-left">
          <thead className="bg-indigo-600 text-white">
            <tr>
              <th className="px-6 py-3">Cryptocurrency</th>
              <th className="px-6 py-3">Price</th>
              <th className="px-6 py-3">Volume (24h)</th>
            </tr>
          </thead>
          <tbody className="bg-gray-50">
            {cryptos.map((crypto, index) => (
              <tr
                key={index}
                className="hover:bg-indigo-100 cursor-pointer transition"
                onClick={() => setSelectedCrypto(crypto)}
              >
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

      {selectedCrypto && (
        <div className="bg-white p-6 rounded-lg shadow-lg text-gray-800">
          <h3 className="text-xl font-semibold mb-4">📝 Order for {selectedCrypto.name} ({selectedCrypto.symbol})</h3>
          <BuySellForm
            symbol={selectedCrypto.symbol}
            price={selectedCrypto.price}
            onOrderPlaced={triggerRefresh}
          />
        </div>
      )}
    </div>
  );
};

export default Home;