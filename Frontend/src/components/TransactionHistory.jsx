import { useEffect, useState } from "react";
import axios from "axios";

const demoAccountId = 1;

const TransactionHistory = ({ refreshFlag }) => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios.get(`/api/accounts/${demoAccountId}/orders`)
      .then(res => {
        console.log("Orders fetched:", res.data);
        setOrders(res.data);
      })
      .catch(err => console.error("Failed to load orders:", err));
  }, [refreshFlag]);

  console.log("Orders:", orders);

  return (
    <div className="p-4 bg-white rounded-xl shadow-md max-w-5xl mx-auto">
      <h2 className="text-2xl font-semibold mb-4 text-center">Transaction History</h2>
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm border border-gray-300">
          <thead className="bg-gray-100">
            <tr>
              <th className="border px-4 py-2">Date</th>
              <th className="border px-4 py-2">Type</th>
              <th className="border px-4 py-2">Symbol</th>
              <th className="border px-4 py-2">Quantity</th>
              <th className="border px-4 py-2">Price</th>
              <th className="border px-4 py-2">Total</th>
              <th className="border px-4 py-2">P/L</th>
            </tr>
          </thead>
          <tbody>
          {Array.isArray(orders) && orders.map((order, idx) => {
              const total = parseFloat(order.price) * parseFloat(order.quantity);
              const isSell = order.type === "SELL";
              const pl = order.profitLoss;

              return (
                <tr key={idx} className="text-center">
                  <td className="border px-4 py-1">
                    {new Date(order.timeOrdered).toLocaleString()}
                  </td>
                  <td className="border px-4 py-1">{order.type}</td>
                  <td className="border px-4 py-1">{order.symbol}</td>
                  <td className="border px-4 py-1">{order.quantity}</td>
                  <td className="border px-4 py-1">${parseFloat(order.price).toFixed(2)}</td>
                  <td className="border px-4 py-1">${total.toFixed(2)}</td>
                  <td className={`border px-4 py-1 font-semibold ${isSell ? (pl > 0 ? 'text-green-600' : 'text-red-600') : 'text-gray-500'}`}>
                    {isSell ? `$${parseFloat(pl || 0).toFixed(2)}` : '—'}
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

export default TransactionHistory;
