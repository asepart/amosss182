import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateTicketButton from './UpdateTicketButton';
import TicketCreate from './TicketCreate';
import DeleteTicketButton from './DeleteTicketButton';
import TicketChatButton from '../Chat/TicketChatButton';
import TicketDetailButton from './TicketDetailButton';
import 'react-table/react-table.css';
import '../../index.css';
import { Link } from 'react-router-dom';

export default class TicketList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		this.fetchData();
	}

	componentDidUpdate() {
		this.updateData();
	}

	fetchData() {
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
		this.updateData()
	}

	updateData() {
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
				Footer: props => <TicketCreate project={this.state.project} name={this.state.name}/>
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
				maxWidth: 55,
				Cell: props => <UpdateTicketButton tick={props} project={this.state.project} name={this.state.name}/>
			}, {
				Header: '',
				accessor: '',
				maxWidth: 75,
				Cell: props => <DeleteTicketButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name}/>
			}
		]

		return (
			<View>
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
							title = {"Users of "  + this.state.name}
							color = "#0e4a80"
						/>
						</Link>
					</View>
				</View>
				<ReactTable
						data={this.state.dataSource}
						noDataText="No Tickets found!"
						minRows={this.state.dataSource.length}
						showPagination={false}
						columns={columns}
				/>
			</View>
		);
	}
}
