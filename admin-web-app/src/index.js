import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import {ProjectList} from './ProjectList.js';

import registerServiceWorker from './registerServiceWorker';

ReactDOM.render(
	<ProjectList />, document.getElementById('root'));
registerServiceWorker();
