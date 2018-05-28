import React, {Component} from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Login from './components/Login/login';
import ProjectList from './components/Project/ProjectList';
import ProjectAdd from './components/Project/ProjectAdd';
import DeleteProjectConfirm from './components/Project/DeleteProjectConfirm';
import DeleteTicketConfirm from './components/Project/DeleteTicketConfirm';
import TicketCreate from './components/Project/TicketCreate';
import TicketList from './components/Project/TicketList';
import TicketChat from './components/Chat/TicketChat';
import DeleteUserConfirm from './components/Project/DeleteUserConfirm';
import UserList from './components/Project/UserList';
import UserAdd from './components/Project/UserAdd';
import Header from './components/shared/Header';
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';
import {isAuth} from './components/shared/auth';
import { BrowserRouter, Route, Link, Switch } from 'react-router-dom'

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
		switch (this.state.show){
			case 'addProject':
				return (<ProjectAdd project={this.state.param} name={this.state.name}/>);
			case 'deleteProject':
				return (<DeleteProjectConfirm project={this.state.param} name={this.state.name}/>);
			case 'deleteTicket':
				return (<DeleteTicketConfirm project={this.state.param} name={this.state.name} id={this.state.id} tName = {this.state.tName}/>);
			case 'deleteUser':
					return (<DeleteUserConfirm project={this.state.param} name={this.state.name} id={this.state.id} firstName={this.state.firstName} lastName={this.state.lastName}/>);
			case 'addUser':
				return (<UserAdd project={this.state.param} id={this.state.id} password={this.state.password} firstName={this.state.firstName} lastName={this.state.lastName} phone={this.state.phone}/>);
			case 'createTicket':
				return (<TicketCreate project={this.state.param} name={this.state.name}
						tName = {this.state.tName}
						tSummary = {this.state.tSummary}
						tDescription = {this.state.tDescription}
						tCategory = {this.state.tCategory}
						tRequiredObservations = {this.state.tRequiredObservations}
						tId = {this.state.tId}/>);
			case 'ticketChat':
				return (<TicketChat project={this.state.param} name={this.state.name} id={this.state.id} tName={this.state.tName}/>);
		};
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
