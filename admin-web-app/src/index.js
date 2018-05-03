import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import {Text} from 'react-native';
import './index.css';
import Login from './components/Login/login';
import ProjectList from './components/Project/ProjectList';
import ProjectAdd from './components/Project/ProjectAdd';
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';

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
	}

	render() {
		if(!this.state.isAuth){
			return(<Login />);
		}
		switch (this.state.show){
			case 'addProject':
				return (<ProjectAdd/>);
			case 'listUsers':
				return (<Text>show Project</Text>); //(<listUsers project={this.state.param}/>);
			case 'addUser':
				return (<Text>add User</Text>); //(<addUser project={this.state.param}/>);
			default:
				return (<ProjectList/>);
		}
	}
}

ReactDOM.render(<Page />, document.getElementById('root'));
registerServiceWorker();
