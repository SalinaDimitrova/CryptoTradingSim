import { useEffect, useState } from 'react';
import axios from 'axios';

const Portfolio = () => {
  const [portfolio, setPortfolio] = useState([]);

  useEffect(() => {
    axios.get('/api/portfolio')  // Make sure the correct endpoint is used
      .then((response) => {
        setPortfolio(response.data); // Assume response is a list of user's holdings
      })
      .catch((error) => {
        console.error("Error fetching portfolio data:", error);
      });
  }, []);

  return (
    <div>
      <h2 className="text-2xl font-semibold text-center my-6">Your Portfolio</h2>
      <div className="overflow-x-auto mb-8">
        <table className="min-w-full text-sm text-left">
          <thead className="bg-gray-200">
            <tr>
              <th className="px-4 py-2">Symbol</th>
              <th className="px-4 py-2">Quantity</th>
              <th className="px-4 py-2">Price</th>
              <th className="px-4 py-2">Total Value</th>
            </tr>
          </thead>
          <tbody>
            {portfolio.map((holding, index) => (
              <tr key={index}>
                <td className="px-4 py-2">{holding.symbol}</td>
                <td className="px-4 py-2">{holding.quantity}</td>
                <td className="px-4 py-2">${holding.price.toFixed(2)}</td>
                <td className="px-4 py-2">${(holding.quantity * holding.price).toFixed(2)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default Portfolio;
