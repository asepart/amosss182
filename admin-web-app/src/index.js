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
import registerServiceWorker from './registerServiceWorker';
import {registerFunc, getState} from './components/shared/GlobalState';
import {isAuth} from './components/shared/auth';

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
			lastName: getState().lastName
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
				return (<ProjectAdd project={this.state.param} name={this.state.name}/>);
			case 'deleteProject':
				return (<DeleteProjectConfirm project={this.state.param} name={this.state.name}/>);
			case 'deleteTicket':
				return (<DeleteTicketConfirm project={this.state.param} name={this.state.name} id={this.state.id} tName = {this.state.tName}/>);
			case 'deleteUser':
					return (<DeleteUserConfirm project={this.state.param} name={this.state.name} id={this.state.id} firstName={this.state.firstName} lastName={this.state.lastName}/>);
			case 'listUsers':
				return (<UserList project={this.state.param} name={this.state.name}/>);
			case 'addUser':
				return (<UserAdd project={this.state.param}/>);
			case 'createTicket':
				return (<TicketCreate project={this.state.param} name={this.state.name}
						tName = {this.state.tName}
						tSummary = {this.state.tSummary}
						tDescription = {this.state.tDescription}
						tCategory = {this.state.tCategory}
						tRequiredObservations = {this.state.tRequiredObservations}
						tId = {this.state.tId}/>);
			case 'showTickets':
				return (<TicketList project={this.state.param} name={this.state.name}
				tName = {this.state.tName}
				tSummary = {this.state.tSummary}
				tDescription = {this.state.tDescription}
				tCategory = {this.state.tCategory}
				tRequiredObservations = {this.state.tRequiredObservations}
				tId = {this.state.tId}/>);
			case 'ticketChat':
				return (<TicketChat project={this.state.param} name={this.state.name} id={this.state.id}/>);
			default:
				return (<ProjectList/>);
		}
	}
}

ReactDOM.render(<Page />, document.getElementById('root'));
registerServiceWorker();
