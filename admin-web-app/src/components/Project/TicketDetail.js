import React, { Component } from 'react';
import {ActivityIndicator,Button, View, Text} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import 'react-table/react-table.css';
import '../../index.css';
import { Link } from 'react-router-dom';
import { File } from './File';

var tmp_ticket;

export default class TicketDetail extends Component {
	constructor(props) {
		super(props);
		if (this.props.isSub) {
			this.state = {
				isDataLoading: true,
				isStatisticsLoading: true,
				keyProj: this.props.keyProj,
				idTicket: this.props.idTicket,
				files: null
			}
		} else {
			this.state = {
				isDataLoading: true,
				isStatisticsLoading: true,
				keyProj: this.props.match.params.project,
				idTicket: this.props.match.params.id,
				files: null
			}
		}
	}

	componentDidMount() {
		fetch(URL + '/projects/' + this.state.keyProj, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				name: responseJson.name,
				project: this.state.keyProj
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
		fetch(URL + '/tickets/' + this.state.idTicket, {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isDataLoading: false,
				data: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
		fetch(URL + '/projects/' + this.state.keyProj + '/tickets', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				statistics: responseJson
			}, function() {});
			if (this.state.statistics !== undefined) {
				for(var i=0; i < this.state.statistics.length; i++) {
					if(this.state.statistics[i].id === this.state.idTicket) {
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

		fetch(URL + '/tickets/' + this.state.idTicket + '/attachments', {
			method: 'GET',
			headers: getAuth(),
		})
		.then(response => response.json())
		.then(response => this.setState({files: response}))
		.catch(e => console.error(e));
	}

	listFiles () {
		return this.state.files.map(file => {
			return (
				<View>
					<File name={file}/>
				</View>
			);
		});
	}

	_renderChatButton() {
		if (this.props.isSub) {
			return null;
		} else {
			return (
				<Button
					onPress = { function doNothing() {} }
					title = "Chat"
					color = "#0c3868"
				/>
			);
		}
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
					<Text><b>Attachments:</b></Text>
					{
						this.state.files === null ?
							<Text>No files uploaded</Text> :
							this.listFiles()
					}
					<Text><br/><b>Description:</b> {'\n' + this.state.data.description}</Text>
				</View>
				<View>
					<Link to={ '/projects/' + this.state.project + '/tickets/' + this.state.idTicket + '/chat'}>
						{this._renderChatButton()}
					</Link>
				</View>
			</View>
		);
	}
}
