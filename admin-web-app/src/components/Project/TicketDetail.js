import React, { Component } from 'react';
import {ActivityIndicator,Button, View, Text} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import 'react-table/react-table.css';
import '../../index.css';
import { Link } from 'react-router-dom';

var tmp_ticket;

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
				name: responseJson.name,
				project: this.props.match.params.project
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
		fetch(URL + '/tickets/' + this.props.match.params.id, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isDataLoading: false,
				data: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
		fetch(URL + '/projects/' + this.props.match.params.project + '/tickets', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				statistics: responseJson
			}, function() {});
			if (this.state.statistics !== undefined) {
				for(var i=0; i < this.state.statistics.length; i++) {
					if(this.state.statistics[i].id === this.props.match.params.id) {
						tmp_ticket = i;
					}
				}
			}
			this.setState({
				isStatisticsLoading: false,
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	render() {
		if (this.state.isDataLoading || this.state.isStatisticsLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}
		return (
			<View>
				<View style={{borderWidth: 0.5}}>
					<Text><b>ID:</b> {this.state.data.id}</Text>
					<Text><br/><b>Project Name:</b> {this.state.name}</Text>
					<Text><b>Project Entry Code:</b> {this.state.project}</Text>
					<Text><br/><b>Name:</b> {this.state.data.name}</Text>
					<Text><b>Summary:</b> {this.state.data.summary}</Text>
					<Text><br/><b>Category:</b> {this.state.data.category}</Text>
					<Text><br/><b>Required observations:</b> {this.state.data.requiredObservations}</Text>
					<Text><br/><b>U:</b> {this.state.statistics[tmp_ticket].U}</Text>
					<Text><b>UP:</b> {this.state.statistics[tmp_ticket].UP}</Text>
					<Text><b>ON:</b> {this.state.statistics[tmp_ticket].ON}</Text>
					<Text><b>OP:</b> {this.state.statistics[tmp_ticket].OP}</Text>
					<Text><br/><b>Status:</b> {this.state.data.status}</Text>
					<Text><br/><b>Description:</b> {'\n' + this.state.data.description}</Text>
				</View>
				<View>
					<Link to={ '/projects/' + this.state.project + '/tickets/' + this.props.match.params.id + '/chat'}>
					<Button
						onPress = { function doNothing() {} }
						title = "Chat"
						color = "#0c3868"
					/>
					</Link>
				</View>
			</View>
		);
	}
}
