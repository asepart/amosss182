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
			isStatisticsLoading: true
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
		var url = URL + '/projects/' + this.props.match.params.project + '/tickets/' + this.props.match.params.id;
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
		var url = URL + '/statistics/' + this.props.match.params.id;
		fetch(url, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isStatisticsLoading: false,
				statisitcs: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	render() {
		if (this.state.isDataLoading || this.state.isStatisticsLoading ) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		return (
			<View>
				<View>
					<Text>ID:		 { this.state.data.id }</Text>
					<Text>Project:	 { this.state.name }</Text>
					<Text>Project key: {this.state.project} </Text>
					<Text>Required observations: { this.state.data.requiredObservations }</Text>
					<Text>Category: { this.state.data.ticketCategory }</Text>
					<Text>Name: { this.state.data.ticketName }</Text>
					<Text>Summary: { this.state.data.ticketSummary }</Text>
					<Text>Statistic U: { this.state.statisitcs.U }</Text>
					<Text>Statistic UP: { this.state.statisitcs.UP }</Text>
					<Text>Statistic ON: { this.state.statisitcs.ON }</Text>
					<Text>Statistic OP: { this.state.statisitcs.OP }</Text>
					<Text>Description: { this.state.data.ticketDescription }</Text>
				</View>
				<View>
					<Link to={"/projects/" + this.state.project }>
					<Button
						onPress = { function doNothing() {} }
						title = "Back to Project"
						color = "#0e4a80"
					/>
					</Link>
				</View>
				<View>
					<Link to={ '/projects/' + this.state.project + '/tickets/' + this.props.match.params.id + '/chat'}>
						<Text>Chat:</Text>
					</Link>
				</View>
			</View>
		);
	}
}
