// pages/History.jsx
import { useEffect, useState } from 'react';
import axios from 'axios';

const History = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios.get('/api/orders')
      .then((res) => setOrders(res.data))
      .catch((err) => console.error("Failed to load orders:", err));
  }, []);

  const totalPL = orders.reduce((acc, o) => acc + (o.profitLoss ?? 0), 0);

  return (
    <section className="pt-28 pb-20 bg-gray-950 text-white min-h-screen px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-3xl font-bold mb-4 text-center">📜 Transaction History</h1>

        <div className="bg-white rounded-lg shadow p-4 mb-6 w-full md:w-1/2 mx-auto text-center text-gray-800">
          <h3 className="text-lg font-semibold">Overall P/L</h3>
          <p className={`text-2xl font-bold ${totalPL >= 0 ? 'text-green-600' : 'text-red-600'}`}>
            {totalPL >= 0 ? '+' : ''}${totalPL.toFixed(2)}
          </p>
        </div>

        <div className="overflow-x-auto bg-white text-gray-800 rounded-lg shadow-lg">
          <table className="min-w-full text-sm text-left">
            <thead className="bg-teal-600 text-white">
              <tr>
                <th className="px-6 py-3">Date</th>
                <th className="px-6 py-3">Type</th>
                <th className="px-6 py-3">Symbol</th>
                <th className="px-6 py-3">Quantity</th>
                <th className="px-6 py-3">Price at Order</th>
                <th className="px-6 py-3">Total</th>
                <th className="px-6 py-3">P/L</th>
              </tr>
            </thead>
            <tbody className="bg-gray-50">
              {orders.map((o, i) => {
                const total = o.quantity * o.price;
                const pl = o.type === 'Sell' ? o.profitLoss : null;

                return (
                  <tr key={i} className="hover:bg-gray-100 transition">
                    <td className="px-6 py-4">{new Date(o.timeOrdered).toLocaleString()}</td>
                    <td className="px-6 py-4">{o.type}</td>
                    <td className="px-6 py-4">{o.symbol}</td>
                    <td className="px-6 py-4">{o.quantity}</td>
                    <td className="px-6 py-4">${o.price.toFixed(2)}</td>
                    <td className="px-6 py-4">${total.toFixed(2)}</td>
                    <td className="px-6 py-4">
                      {pl !== null ? (
                        <span className={`flex items-center gap-1 font-semibold ${pl >= 0 ? 'text-green-600' : 'text-red-600'}`}>
                          {pl >= 0 ? '▲' : '▼'}
                          {pl >= 0 ? '+' : ''}${pl.toFixed(2)}
                        </span>
                      ) : '—'}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </section>
  );
};

export default History;