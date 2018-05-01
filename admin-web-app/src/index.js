import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import Login from './components/Login/login';
import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(<Login />, document.getElementById('root'));
registerServiceWorker();


