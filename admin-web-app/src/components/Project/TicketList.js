import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateTicketButton from './UpdateTicketButton';
import { setState } from '../shared/GlobalState';
import 'react-table/react-table.css';
import '../../index.css';

var pickerPlaceholder = "Category";

export default class TicketList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		var url = URL;
		url += '/projects/' + this.props.project + '/tickets';
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
			param: this.props.project,
			name: this.props.name
		});
	}
	
	showCreateTicket () {
		setState({
			isAuth: true,
			show: 'createTicket',
			param: this.props.project,
			name: this.props.name,
			tName: '',
			tSummary: '',
			tDescription: '',
			tCategory: pickerPlaceholder,
			tRequiredObservations: '',
			tId: '0'
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
				Cell: props => <UpdateTicketButton tick={props} project={this.props.project} name={this.props.name}/>
			}/*, {	//TODO:
				Header: '',
				accessor: '',
				Cell: props => <DeleteTicketButton tick={props}/>
			}*/
		]

		return (
			<View>
				<Button
					disabled = {true}
					title = {"Tickets of " + this.props.name}
				/>
				<Button
					onPress = { this.showCreateTicket.bind(this) }
					title = "Create Ticket"
					color = "#0c3868"
				/>
				<Button
					onPress = { this.showUserManagement.bind(this) }
					title = "Back"
					color = "#0e4a80"
				/>
				<ReactTable data={this.state.dataSource} columns={columns}/>
			</View>
		);
	}
}
