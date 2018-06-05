import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Login from './components/Login/login';
import ProjectList from './components/Project/ProjectList';
import TicketList from './components/Project/TicketList';
import TicketDetail from './components/Project/TicketDetail';
import TicketChat from './components/Chat/TicketChat';
import UserList from './components/Project/UserList';
import Header from './components/shared/Header';
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';
import {isAuth} from './components/shared/auth';
import { BrowserRouter, Route, Switch } from 'react-router-dom'

class Page extends Component{
	handleGlobalState (){
		this.setState ({
			isAuth: getState().isAuth,
			show: getState().show,
			param: getState().param,
			name: getState().name,
			tName: getState().tName,
			tSummary: getState().tSummary,
			tDescription: getState().tDescription,
			tCategory: getState().tCategory,
			tRequiredObservations: getState().tRequiredObservations,
			tId: getState().tId,
			id: getState().id,
			firstName: getState().firstName,
			lastName: getState().lastName,
			phone: getState().phone,
			password: getState().password
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
		return(
			<Switch>
				<Route exact path="/" component={ProjectList}/>
				<Route path="/usermanagement" render={props => <UserList project={this.state.param} name={this.state.name} {...props} />}/>
				<Route exact path='/projects/:project' render={props => <TicketList project={this.state.param} name={this.state.name}
																			tName = {this.state.tName}
																			tSummary = {this.state.tSummary}
																			tDescription = {this.state.tDescription}
																			tCategory = {this.state.tCategory}
																			tRequiredObservations = {this.state.tRequiredObservations}
																			tId = {this.state.tId} {...props} />}/>
				<Route exact path='/projects/:project/tickets/:id' component={TicketDetail} />
				<Route path='/projects/:project/tickets/:id/chat' render={props => <TicketChat project={this.state.param}
																			name={this.state.name}
																			id={this.state.id}
																			tName={this.state.tName} {...props} />}/>
				<Route path='/projects/:project/users' render={props => <UserList project={this.state.param} name={this.state.name}
																			tName = {this.state.tName}
																			tSummary = {this.state.tSummary}
																			tDescription = {this.state.tDescription}
																			tCategory = {this.state.tCategory}
																			tRequiredObservations = {this.state.tRequiredObservations}
																			tId = {this.state.tId} {...props} />}/>
			</Switch>
		)
	}
}

class App extends Component{
	handleAuthState (){
		this.setState ({
			isAuth: getState().isAuth,
			isLoading: getState().isLoading,
			firstName: getState().firstName,
			lastName: getState().lastName,
			phone: getState().phone,
			password: getState().password
		});
	}
	constructor () {
		super ();
		this.state = {
			isAuth: false,
		};

		registerFunc (this.handleAuthState.bind(this));
		this.checkAuth();
	}

	async checkAuth () {
		if (await isAuth())
			this.setState({
				isAuth: true,
			});
	}

	render() {
		if(!this.state.isAuth){
			return(<Login />);
		}
		else {
			return (
				<div>
					<Header />
					<Page />
				</div>
			)
		}
	}
}

ReactDOM.render((
	<BrowserRouter>
		<App />
	</BrowserRouter>
), document.getElementById('root'));
registerServiceWorker();
