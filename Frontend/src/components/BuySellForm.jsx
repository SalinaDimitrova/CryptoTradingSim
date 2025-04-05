// BuySellForm.jsx
import { useState } from 'react';
import axios from 'axios';

const BuySellForm = ({ symbol, price, accountId, onOrderPlaced }) => {
  const [quantity, setQuantity] = useState('');
  const [type, setType] = useState('BUY');

  const id = accountId

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('/api/accounts/orders', 
      {
        symbol,
        price,
        quantity: parseFloat(quantity),
        type,
      });
      onOrderPlaced();
    } catch (err) {
      console.error("Order failed:", err);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="text-lg font-semibold">
        Trading: <span className="text-indigo-600">{symbol}</span>
      </div>

      <input
        type="number"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
        placeholder="Quantity"
        required
        className="w-full px-4 py-3 rounded-lg bg-gray-100 border border-gray-300 text-gray-800 focus:ring-2 focus:ring-blue-600 focus:outline-none transition"
      />

      <input
        type="number"
        value={price}
        disabled
        className="w-full px-4 py-3 rounded-lg bg-gray-200 border border-gray-300 text-gray-600 cursor-not-allowed"
      />

      <select
        value={type}
        onChange={(e) => setType(e.target.value)}
        className="w-full px-4 py-3 rounded-lg bg-white border border-gray-300 text-gray-800"
      >
         <option value="BUY">BUY</option>
         <option value="SELL">SELL</option>
      </select>

      <button
        type="submit"
        className="w-full bg-blue-600 hover:bg-blue-700 text-white py-3 rounded-lg font-semibold transition duration-200 shadow-md hover:shadow-lg"
      >
        ✅ Confirm Order
      </button>
    </form>
  );
};

export default BuySellForm;
