import { useEffect, useState } from 'react';
import axios from 'axios';

const History = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios.get('/api/orders')  // Fetch orders (replace with the correct endpoint)
      .then((response) => {
        setOrders(response.data); // Assume response is a list of orders
      })
      .catch((error) => {
        console.error("Error fetching orders:", error);
      });
  }, []);

  return (
    <div>
      <h2 className="text-2xl font-semibold text-center my-6">Transaction History</h2>
      <div className="overflow-x-auto mb-8">
        <table className="min-w-full text-sm text-left">
          <thead className="bg-gray-200">
            <tr>
              <th className="px-4 py-2">Date</th>
              <th className="px-4 py-2">Type</th>
              <th className="px-4 py-2">Symbol</th>
              <th className="px-4 py-2">Quantity</th>
              <th className="px-4 py-2">Price</th>
              <th className="px-4 py-2">Total</th>
              <th className="px-4 py-2">P/L</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order, index) => {
              const total = parseFloat(order.price) * parseFloat(order.quantity);
              const pl = order.profitLoss;

              return (
                <tr key={index}>
                  <td className="px-4 py-2">{new Date(order.timeOrdered).toLocaleString()}</td>
                  <td className="px-4 py-2">{order.type}</td>
                  <td className="px-4 py-2">{order.symbol}</td>
                  <td className="px-4 py-2">{order.quantity}</td>
                  <td className="px-4 py-2">${order.price.toFixed(2)}</td>
                  <td className="px-4 py-2">${total.toFixed(2)}</td>
                  <td className="px-4 py-2">{pl ? `$${pl.toFixed(2)}` : '—'}</td>
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
