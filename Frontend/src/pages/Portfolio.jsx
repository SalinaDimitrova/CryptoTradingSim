import { useEffect, useState } from 'react';
import axios from 'axios';

const demoAccountId = 1;

const Portfolio = ({ prices }) => {
  const [portfolio, setPortfolio] = useState([]);
  const [account, setAccount] = useState(null);
  const [isResetting, setIsResetting] = useState(false);

  const fetchPortfolio = () => {
    axios.get(`/api/portfolios`)
      .then(res => setPortfolio(res.data))
      .catch(err => console.error("Error loading portfolio:", err));
  };

  const fetchAccount = () => {
    axios.get(`/api/accounts/${demoAccountId}`)
      .then(res => setAccount(res.data))
      .catch(err => console.error("Error loading account:", err));
  };

  useEffect(() => {
    fetchPortfolio();
    fetchAccount();
  }, []);

  const handleReset = async () => {
    if (!window.confirm("Are you sure you want to reset your account to $10,000 and remove all crypto?")) return;

    setIsResetting(true);
    try {
      await axios.post(`/api/accounts/${demoAccountId}/reset`);
      fetchPortfolio();
      fetchAccount();
    } catch (err) {
      console.error("Reset failed:", err);
      alert("Failed to reset the account.");
    } finally {
      setIsResetting(false);
    }
  };

  return (
    <section className="pt-28 pb-20 bg-gray-950 text-white min-h-screen px-4">
      <div className="max-w-5xl mx-auto">
        <h1 className="text-3xl font-bold mb-4 text-center">📊 Your Portfolio</h1>

        {/* 🔁 Reset Button */}
        <div className="text-center mb-6">
          <button
            onClick={handleReset}
            disabled={isResetting}
            className="bg-pink-600 hover:bg-purple-700 text-white px-6 py-2 rounded-full font-semibold transition shadow-lg hover:shadow-xl disabled:opacity-50"
          >
            {isResetting ? 'Resetting...' : '🔁 Reset Account'}
          </button>
        </div>

        {/* 💰 Account Balance */}
        <div className="text-xl font-semibold text-center mb-6">
          Account Balance: {account ? `$${account.balance.toFixed(2)}` : 'Loading...'}
        </div>

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
              {portfolio.length === 0 ? (
                <tr>
                  <td className="px-6 py-4" colSpan="3">No assets in portfolio.</td>
                </tr>
              ) : (
                portfolio.map((asset, idx) => (
                  <tr key={idx} className="hover:bg-gray-100 transition">
                    <td className="px-6 py-4">{asset.symbol}</td>
                    <td className="px-6 py-4">{asset.quantity}</td>
                    <td className="px-6 py-4">
                      {prices[asset.symbol]
                        ? `$${prices[asset.symbol].toFixed(2)}`
                        : 'Loading...'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
};

export default Portfolio;
