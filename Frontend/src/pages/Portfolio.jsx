// pages/Portfolio.jsx
import { useEffect, useState } from 'react';
import axios from 'axios';

const Portfolio = () => {
  const [portfolio, setPortfolio] = useState([]);
  const [prices, setPrices] = useState({});

  useEffect(() => {
    axios.get('/api/portfolio')
      .then((res) => setPortfolio(res.data))
      .catch((err) => console.error("Error loading portfolio:", err));
  }, []);

  useEffect(() => {
    if (portfolio.length > 0) {
      const symbols = portfolio.map(asset => asset.symbol).join(',');
      axios.get(`/api/kraken-prices?symbols=${symbols}`)
        .then(res => setPrices(res.data))
        .catch(err => console.error("Error loading prices:", err));
    }
  }, [portfolio]);

  return (
    <section className="pt-28 pb-20 bg-gray-950 text-white min-h-screen px-4">
      <div className="max-w-5xl mx-auto">
        <h1 className="text-3xl font-bold mb-4 text-center">📊 Your Portfolio</h1>

        <div className="overflow-x-auto bg-white text-gray-800 rounded-lg shadow-lg">
          <table className="min-w-full text-sm text-left">
            <thead className="bg-blue-600 text-white">
              <tr>
                <th className="px-6 py-3">Symbol</th>
                <th className="px-6 py-3">Quantity</th>
                <th className="px-6 py-3">Current Price</th>
              </tr>
            </thead>
            <tbody className="bg-gray-50">
              {portfolio.map((asset, idx) => (
                <tr key={idx} className="hover:bg-gray-100 transition">
                  <td className="px-6 py-4">{asset.symbol}</td>
                  <td className="px-6 py-4">{asset.quantity}</td>
                  <td className="px-6 py-4">
                    {prices[asset.symbol] ? `$${prices[asset.symbol].toFixed(2)}` : 'Loading...'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
};

export default Portfolio;