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
				<View>
					<Text>ID:		 { this.state.data.id }</Text>
					<Text>Project:	 { this.state.name }</Text>
					<Text>Project key: {this.state.project} </Text>
					<Text>Required observations: { this.state.data.requiredObservations }</Text>
					<Text>Category: { this.state.data.category }</Text>
					<Text>Name: { this.state.data.name }</Text>
					<Text>Summary: { this.state.data.summary }</Text>
					<Text>Statistic U: { this.state.statistics[tmp_ticket].U }</Text>
					<Text>Statistic UP: { this.state.statistics[tmp_ticket].UP }</Text>
					<Text>Statistic ON: { this.state.statistics[tmp_ticket].ON }</Text>
					<Text>Statistic OP: { this.state.statistics[tmp_ticket].OP }</Text>
					<Text>Description: { this.state.data.description }</Text>
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
