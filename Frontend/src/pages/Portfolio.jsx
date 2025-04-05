import { useEffect, useState } from 'react';
import axios from 'axios';

const Portfolio = () => {
  const [portfolio, setPortfolio] = useState([]);

  useEffect(() => {
    axios.get('/api/portfolio')
      .then((response) => setPortfolio(response.data))
      .catch((error) => console.error("Error fetching portfolio data:", error));
  }, []);

  const totalBalance = portfolio.reduce((acc, item) => acc + (item.quantity * item.price), 0);

  return (
    <div className="max-w-7xl mx-auto py-6 px-4">
      <h2 className="text-3xl font-semibold text-center text-gray-900 my-6">Your Portfolio</h2>
      <h3 className="text-xl font-semibold text-right mb-4">Balance: ${totalBalance.toFixed(2)}</h3>

      <div className="overflow-x-auto shadow-lg rounded-lg bg-white">
        <table className="min-w-full text-sm text-left text-gray-700">
          <thead className="bg-blue-500 text-white">
            <tr>
              <th className="px-6 py-3">Symbol</th>
              <th className="px-6 py-3">Quantity</th>
              <th className="px-6 py-3">Price</th>
              <th className="px-6 py-3">Total Value</th>
            </tr>
          </thead>
          <tbody className="bg-gray-50">
            {portfolio.map((holding, index) => (
              <tr key={index} className="hover:bg-gray-100">
                <td className="px-6 py-4">{holding.symbol}</td>
                <td className="px-6 py-4">{holding.quantity}</td>
                <td className="px-6 py-4">${holding.price.toFixed(2)}</td>
                <td className="px-6 py-4">${(holding.quantity * holding.price).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Portfolio;