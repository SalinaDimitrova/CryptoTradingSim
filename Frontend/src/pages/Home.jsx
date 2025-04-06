import { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import { CompatClient, Stomp } from '@stomp/stompjs';
import BuySellForm from '../components/BuySellForm';

const demoAccountId = 1;

const trackedSymbols = [
  "BTC/USD", "ETH/USD", "BNB/USD", "XRP/USD", "ADA/USD",
  "DOGE/USD", "SOL/USD", "DOT/USD", "MATIC/USD", "LTC/USD",
  "SHIB/USD", "AVAX/USD", "UNI/USD", "XLM/USD", "BCH/USD",
  "ALGO/USD", "VET/USD", "ICP/USD", "MANA/USD", "AXS/USD"
];

const Home = () => {
  const [prices, setPrices] = useState({});
  const [selectedCrypto, setSelectedCrypto] = useState(null);
  const stompClientRef = useRef(/** @type {CompatClient | null} */(null));

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws'); // Spring Boot WebSocket endpoint
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    stompClient.connect({}, () => {
      stompClient.subscribe('/topic/prices', (message) => {

        const data = JSON.parse(message.body);

        if (Array.isArray(data)) {
          const updatedPrices = data.reduce((acc, item) => {
            if (item.symbol && item.last !== undefined) {
              acc[item.symbol] = parseFloat(item.last);
            }
            return acc;
          }, {});
  
          setPrices(prev => ({ ...prev, ...updatedPrices }));
        }
      });
    });

    return () => {
      stompClient.disconnect();
    };
  }, []);

  const formatName = (pair) => {
    const name = pair.split('/')[0];
    return `${name} (${pair})`;
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
            </tr>
          </thead>
          <tbody className="bg-gray-50">
            {trackedSymbols.map((pair, index) => (
              <tr
                key={index}
                className="hover:bg-indigo-100 cursor-pointer transition"
                onClick={() => {
                  const price = prices[pair];
                  if (price) {
                    setSelectedCrypto({
                      symbol: pair,
                      name: pair,
                      price
                    });
                  }
                }}
              >
                <td className="px-6 py-4">{formatName(pair)}</td>
                <td className="px-6 py-4">
                  {prices[pair] ? `$${prices[pair].toFixed(2)}` : 'Loading...'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {selectedCrypto && (
        <div className="bg-white p-6 rounded-lg shadow-lg text-gray-800">
          <h3 className="text-xl font-semibold mb-4">📝 Order for {selectedCrypto.name}</h3>
          <BuySellForm
            symbol={selectedCrypto.symbol}
            price={selectedCrypto.price}
            accountId={demoAccountId}
            onOrderPlaced={() => setSelectedCrypto(null)}
          />
        </div>
      )}
    </div>
  );
};

export default Home;
