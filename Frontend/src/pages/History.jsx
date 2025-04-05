import { useEffect, useState } from 'react';
import axios from 'axios';

const History = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios.get('/api/orders')
      .then((response) => setOrders(response.data))
      .catch((error) => console.error("Error fetching orders:", error));
  }, []);

  const totalPL = orders.reduce((acc, order) => acc + (order.profitLoss ?? 0), 0);

  return (
    <div className="max-w-7xl mx-auto py-6 px-4">
      <h2 className="text-3xl font-semibold text-center text-gray-900 my-6">Transaction History</h2>

      <div className="bg-white rounded-lg shadow p-4 mb-4 w-full md:w-1/2 mx-auto text-center">
        <h3 className="text-lg font-semibold">Overall P/L</h3>
        <p className={`text-2xl font-bold ${totalPL >= 0 ? 'text-green-600' : 'text-red-600'}`}>
          {totalPL >= 0 ? '+' : ''}${totalPL.toFixed(2)}
        </p>
      </div>

      <div className="overflow-x-auto shadow-lg rounded-lg bg-white">
        <table className="min-w-full text-sm text-left text-gray-700">
          <thead className="bg-teal-500 text-white">
            <tr>
              <th className="px-6 py-3">Date</th>
              <th className="px-6 py-3">Type</th>
              <th className="px-6 py-3">Symbol</th>
              <th className="px-6 py-3">Quantity</th>
              <th className="px-6 py-3">Price</th>
              <th className="px-6 py-3">Total</th>
              <th className="px-6 py-3">P/L</th>
            </tr>
          </thead>
          <tbody className="bg-gray-50">
            {orders.map((order, index) => {
              const total = order.price * order.quantity;
              const pl = order.profitLoss;
              return (
                <tr key={index} className="hover:bg-gray-100">
                  <td className="px-6 py-4">{new Date(order.timeOrdered).toLocaleString()}</td>
                  <td className="px-6 py-4">{order.type}</td>
                  <td className="px-6 py-4">{order.symbol}</td>
                  <td className="px-6 py-4">{order.quantity}</td>
                  <td className="px-6 py-4">${order.price.toFixed(2)}</td>
                  <td className="px-6 py-4">${total.toFixed(2)}</td>
                  <td className="px-6 py-4">
                    {pl !== null ? (
                      <span className={`text-${pl >= 0 ? 'green' : 'red'}-500`}>
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
  );
};

export default History;