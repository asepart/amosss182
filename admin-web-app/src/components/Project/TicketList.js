import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateTicketButton from './UpdateTicketButton';
import { setState } from '../shared/GlobalState';
import DeleteTicketButton from './DeleteTicketButton';
import TicketChatButton from '../Chat/TicketChatButton';
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
				Cell: props => <TicketChatButton proj={props} keyFromParent={this.props.project} nameFromParent={this.props.name}/>
			}, {
				Header: '',
				accessor: '',
				Cell: props => <UpdateTicketButton tick={props} project={this.props.project} name={this.props.name}/>
			}, {
				Header: '',
				accessor: '',
				Cell: props => <DeleteTicketButton proj={props} keyFromParent={this.props.project} nameFromParent={this.props.name}/>
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
							disabled = {true}
							title = {"Tickets of " + this.props.name}
						/>
					</View>
					<View style={{flex:1}}>
						<Button
							onPress = { this.showUserManagement.bind(this) }
							title = {"Users of "  + this.props.name}
							color = "#0e4a80"
						/>
					</View>
				</View>
				<ReactTable data={this.state.dataSource} columns={columns} defaultPageSize = {10}/>
				<View>
					<Button
						onPress = { this.showProjectList.bind(this) }
						title = "Back to Projects"
						color = "#0e4a80"
					/>
				</View>
			</View>
		);
	}
}
