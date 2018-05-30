import React, { Component } from 'react';
import {ActivityIndicator,Button, View, Text} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateTicketButton from './UpdateTicketButton';
import { setState } from '../shared/GlobalState';
import DeleteTicketButton from './DeleteTicketButton';
import TicketChatButton from '../Chat/TicketChatButton';
import TicketDetailButton from './TicketDetailButton';
import 'react-table/react-table.css';
import '../../index.css';
import { Link } from 'react-router-dom';

var pickerPlaceholder = "Category";

export default class TicketList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		fetch(URL + '/projects/' + this.props.match.params.project, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				name: responseJson.projectName,
				project: this.props.match.params.project
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
		var url = URL;
		url += '/projects/' + this.props.match.params.project + '/tickets';
		return fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	showUserManagement () {
		setState({
			isAuth: true,
			show: 'listUsers',
			param: this.state.project,
			name: this.state.name
		});
	}

	showCreateTicket () {
		setState({
			isAuth: true,
			show: 'createTicket',
			param: this.state.project,
			name: this.state.name,
			tName: '',
			tSummary: '',
			tDescription: '',
			tCategory: pickerPlaceholder,
			tRequiredObservations: '',
			tId: '0'
		});
	}

	showProjectList () {
		setState({
			isAuth: true,
			show: ''
		});
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		const columns = [
			{
				Header: 'ID',
				accessor: 'id',
			}, {
				Header: 'Name',
				accessor: 'ticketName',
				Cell: props => <TicketDetailButton proj={props} keyProj={this.props.match.params.project}/>
			}, {
				Header: 'Summary',
				accessor: 'ticketSummary',
			}, {
				Header: 'Description',
				accessor: 'ticketDescription'
			}, {
				Header: 'Category',
				accessor: 'ticketCategory'
			}, {
				Header: 'Required observations',
				accessor: 'requiredObservations' // String-based value accessors!
			}, {
				Header: '',
				accessor: '',
				Cell: props => <TicketChatButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name}/>
			}, {
				Header: '',
				accessor: '',
				Cell: props => <UpdateTicketButton tick={props} project={this.state.project} name={this.state.name}/>
			}, {
				Header: '',
				accessor: '',
				Cell: props => <DeleteTicketButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name}/>
			}
		]

		return (
			<View>
				<View>
					<Button
						onPress = { this.showCreateTicket.bind(this) }
						title = "Add Ticket"
						color = "#0c3868"
					/>
				</View>
				<View style={{flexDirection: 'row'}}>
					<View style={{flex:1}}>
						<Button
							onPress = { function doNothing() {} }
							disabled = {true}
							title = {"Tickets of " + this.state.name}
						/>
					</View>
					<View style={{flex:1}}>
						<Link to={"/projects/" + this.state.project + "/users"} style={{textDecoration: 'none'}}>
						<Button
							onPress = { this.showUserManagement.bind(this) }
							title = {"Users of "  + this.state.name}
							color = "#0e4a80"
						/>
						</Link>
					</View>
				</View>
				<ReactTable data={this.state.dataSource} columns={columns} defaultPageSize={10} showPagination={false}/>
				<View>
					<Link to="/">
					<Button
						onPress = { this.showProjectList.bind(this) }
						title = "Back to Projects"
						color = "#0e4a80"
					/>
					</Link>
				</View>
			</View>
		);
	}
}
