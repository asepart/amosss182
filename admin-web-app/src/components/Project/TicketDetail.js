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

export default class TicketDetail extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isDataLoading: true,
			isChatLoading: true
		}
	}

	componentDidMount() {
		var url = URL + '/projects/' + this.props.project + '/tickets/' + this.props.id;
		fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isDataLoading: false,
				data: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});

		url = URL + '/messages/' + this.props.id;
		fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isChatLoading: false,
				chat: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	render() {
		if (this.state.isDataLoading || this.state.isChatLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		const chatColumns = [
			{
				Header: 'From',
				accessor: 'sender',
			}, {
				Header: 'Message',
				accessor: 'content'
			}
		]

		return (
			<View>
				<View>
					<Text>ID:		 { this.state.data.id }</Text>
					<Text>Project:	 { this.state.data.ProjectKey }</Text>
					<Text>Required observations: { this.state.data.requiredObservations }</Text>
					<Text>Category: { this.state.data.ticketCategory }</Text>
					<Text>Description: { this.state.data.ticketDescription }</Text>
					<Text>Name: { this.state.data.ticketName }</Text>
					<Text>Summary: { this.state.data.ticketSummary }</Text>
				</View>

				<View>
					<Text>Chat:</Text>
					<ReactTable data={this.state.chat} columns={chatColumns} defaultPageSize={100} showPagination={false}/>
				</View>
			</View>
		);
	}
}
