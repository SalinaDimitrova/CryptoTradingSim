import React, { useState, useEffect } from 'react';
import axios from 'axios';

const CryptoList = () => {
  const [cryptos, setCryptos] = useState([]); // To store the list of cryptocurrencies

  // Fetch the list of cryptocurrencies when the component mounts
  useEffect(() => {
    axios.get('http://localhost:8080/api/top-cryptos')  // Adjust the URL if needed
      .then(response => {
        setCryptos(response.data);  // Store the fetched data in state
      })
      .catch(error => {
        console.error('Error fetching cryptos:', error);
      });
  }, []);  // Empty array means this effect runs only once when the component mounts

  return (
    <div>
      <h2>Top Cryptocurrencies</h2>
      <ul>
        {cryptos.length > 0 ? (
          cryptos.map((crypto, index) => (
            <li key={index}>
              <strong>{crypto.name} ({crypto.symbol})</strong>
              <br />
              Price: ${crypto.price}
              <br />
              Volume: {crypto.volume}
            </li>
          ))
        ) : (
          <p>Loading cryptocurrencies...</p>
        )}
      </ul>
    </div>
  );
};

export default CryptoList;
