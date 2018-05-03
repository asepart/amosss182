import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Login from './components/Login/login';
import ProjectList from './components/ProjectList';
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';

class Page extends Component{
	handleGlobalState (){
		this.setState ({
			isAuth: getState().isAuth
		});
	}

	constructor (props) {
		super (props);
		this.state = {
			isAuth: false
		};
		registerFunc (this.handleGlobalState.bind(this));
	}

	render() {
		if(!this.state.isAuth){
			return(<Login />);
		}
		return (<ProjectList/>);
	}
}

ReactDOM.render(<Page />, document.getElementById('root'));
registerServiceWorker();
