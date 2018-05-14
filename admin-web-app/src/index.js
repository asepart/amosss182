import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Login from './components/Login/login';
import ProjectList from './components/Project/ProjectList';
import ProjectAdd from './components/Project/ProjectAdd';
import UserList from './components/Project/UserList';
import UserAdd from './components/Project/UserAdd';
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';
import {isAuth} from './components/shared/auth';

class Page extends Component{
	handleGlobalState (){
		this.setState ({
			isAuth: getState().isAuth,
			show: getState().show,
			param: getState().param
		});
	}

	constructor (props) {
		super (props);
		this.state = {
			isAuth: false
		};
		registerFunc (this.handleGlobalState.bind(this));
		this.checkAuth();
	}
	async checkAuth () {
		if (await isAuth())
			this.setState({
				isAuth: true
			});
	}

	render() {
		if(!this.state.isAuth){
			return(<Login />);
		}
		switch (this.state.show){
			case 'addProject':
				return (<ProjectAdd/>);
			case 'listUsers':
				return (<UserList project={this.state.param}/>);
			case 'addUser':
				return (<UserAdd project={this.state.param}/>);
			default:
				return (<ProjectList/>);
		}
	}
}

ReactDOM.render(<Page />, document.getElementById('root'));
registerServiceWorker();
