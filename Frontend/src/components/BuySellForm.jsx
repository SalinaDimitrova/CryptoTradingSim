import { useState } from "react";
import axios from "axios";

const BuySellForm = ({ onOrderPlaced }) => {
  const [formData, setFormData] = useState({
    type: "BUY",
    symbol: "",
    quantity: "",
    price: "",
  });

  const handleChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const payload = {
        accountId: 1,
        type: formData.type, // ✅ this must be 'BUY' or 'SELL'
        symbol: formData.symbol.toUpperCase(),
        quantity: parseFloat(formData.quantity),
        price: parseFloat(formData.price),
      };
      
    console.log("Submitting order:", payload);
    
    try {
      await axios.post("/orders", payload);
      alert("Order placed successfully!");

      // Let parent refresh the order list
      if (onOrderPlaced) onOrderPlaced();
    } catch (err) {
      console.error("Failed to place order:", err);
      alert("Failed to place order. Check console.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white p-4 rounded-xl shadow-md mb-6 max-w-2xl mx-auto">
      <h2 className="text-xl font-semibold mb-4">Place an Order</h2>
      <div className="grid grid-cols-2 gap-4 mb-4">
        <div>
          <label className="block text-sm font-medium">Type</label>
          <select name="type" value={formData.type} onChange={handleChange} className="w-full border rounded px-2 py-1">
            <option value="BUY">BUY</option>
            <option value="SELL">SELL</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium">Symbol</label>
          <input
            type="text"
            name="symbol"
            value={formData.symbol}
            onChange={handleChange}
            className="w-full border rounded px-2 py-1"
            placeholder="e.g. BTC"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium">Quantity</label>
          <input
            type="number"
            name="quantity"
            value={formData.quantity}
            onChange={handleChange}
            className="w-full border rounded px-2 py-1"
            required
          />
        </div>
        <div>
          <label className="block text-sm font-medium">Price</label>
          <input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleChange}
            className="w-full border rounded px-2 py-1"
            required
          />
        </div>
      </div>
      <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
        Submit Order
      </button>
    </form>
  );
};

export default BuySellForm;
