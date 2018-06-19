import React, { Component } from 'react';
import {ActivityIndicator,Button, View, Text} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import UpdateTicketButton from './UpdateTicketButton';
import TicketCreate from './TicketCreate';
import DeleteTicketButton from './DeleteTicketButton';
import TicketChatButton from '../Chat/TicketChatButton';
import TicketDetailButton from './TicketDetailButton';
import TicketStatus from './TicketStatus';
import 'react-table/react-table.css';
import '../../index.css';
import { Link } from 'react-router-dom';
import {getUpdateBoolean, setUpdateBoolean} from '../shared/GlobalState';

export default class TicketList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		this.fetchMetaData();
		this.fetchTickets();
	}

	componentDidUpdate() {
		if(getUpdateBoolean() === true) {
			this.fetchTickets();
			setUpdateBoolean(false);
		}
	}

	fetchMetaData() {
		fetch(URL + '/projects/' + this.props.match.params.project, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				name: responseJson.name,
				project: this.props.match.params.project
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	fetchTickets() {
		fetch(URL + '/projects/' + this.props.match.params.project + '/tickets', {method:'GET', headers: getAuth()})
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
				maxWidth: 40,
				Footer: props => <TicketCreate project={this.state.project} name={this.state.name} callToParent={this.fetchTickets.bind(this)}/>
			}, {
				Header: 'Name',
				accessor: 'name',
				Cell: props => <TicketDetailButton proj={props} keyProj={this.props.match.params.project}/>,
			}, {
				Header: 'Summary',
				accessor: 'summary',
			}, {
				Header: 'Description',
				accessor: 'description'
			}, {
				Header: 'Category',
				accessor: 'category',
				maxWidth: 160,
			}, {
				Header: 'Required observations',
				accessor: 'requiredObservations', // String-based value accessors!
				maxWidth: 180,
			}, {
				Header: 'U',
				accessor: 'U',
				maxWidth: 180,
			}, {
				Header: 'UP',
				accessor: 'UP',
				maxWidth: 180,
			}, {
				Header: 'Status',
				accessor: 'status', // String-based value accessors!
				maxWidth: 95,
				Cell: props => <TicketStatus state={props}/>
			}, {
				Header: '',
				accessor: '',
				maxWidth: 35,
				Cell: props => <TicketChatButton proj={props} keyFromParent={this.state.project} nameFromParent={this.state.name}/>
			}, {
				Header: '',
				accessor: '',
				maxWidth: 35,
				Cell: props => <UpdateTicketButton tick={props} project={this.state.project} name={this.state.name} callToParent={this.fetchTickets.bind(this)}/>
			}, {
				Header: '',
				accessor: '',
				maxWidth: 35,
				Cell: props => <DeleteTicketButton proj={props} project={this.state.project} keyFromParent={this.state.project} nameFromParent={this.state.name} callToParent={this.fetchTickets.bind(this)}/>
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
							onPress = { function doNothing() {} }
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
						SubComponent={row => {
							return (
									
								//TODO: pass data of ticket to subcomponent	
								
								<View style={{borderWidth: 0.5}}>
								<Text><b>ID:</b> {row.id}</Text>
								<Text><br/><b>Project Name:</b> {this.state.name}</Text>
								<Text><b>Project Entry Code:</b> {this.state.project}</Text>
								<Text><br/><b>Name:</b> {row.name}</Text>
								<Text><b>Summary:</b> {this.state.dataSource.summary}</Text>
								<Text><br/><b>Category:</b> {this.state.dataSource.category}</Text>
								<Text><br/><b>Required observations:</b> {this.state.dataSource.requiredObservations}</Text>
								<Text><br/><b>U:</b> {this.state.dataSource.U}</Text>
								<Text><b>UP:</b> {this.state.dataSource.UP}</Text>
								<Text><b>ON:</b> {this.state.dataSource.ON}</Text>
								<Text><b>OP:</b> {this.state.dataSource.OP}</Text>
								<Text><br/><b>Status:</b> {this.state.dataSource.status}</Text>
								<Text><br/><b>Description:</b> {'\n' + this.state.dataSource.description}</Text>
								</View>
							)
						}}
				/>
			</View>
		);
	}
}
