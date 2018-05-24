import React, {Component} from 'react';
import {Button,ActivityIndicator,View} from 'react-native';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import { setState } from '../shared/GlobalState';
import '../../index.css';

export default class DeleteTicketConfirm extends Component {

	constructor(props) {
		super(props);
		this.state = {
			id: this.props.id,
			entryKey: this.props.project,
			tName: this.props.tName
		};
	}

  showTicketList () {
		setState({
			isAuth: true,
			show: 'showTickets',
			param: this.props.project,
			name: this.props.name
		});
	}

  async deleteTicket() {
    var url = URL;
		url += '/projects/' + this.state.entryKey + '/tickets/' + this.state.id;
    await fetch(url, {method:'DELETE', headers: getAuth()})
      .then((response) => response.json())
      .catch((error) => {
        console.error(error);
      });
    this.showTicketList ();
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style = {{flex: 1, padding: 20}}>
					<ActivityIndicator / >
				</View>
			)
		}
		return (
			<View>
			<Button
				disabled = {true}
				title = {"Delete " + this.state.tName + " from " + this.props.name + "?"}
			/>
			<Button onPress = { this.deleteTicket.bind(this) } title = "Delete" color = "#0c3868"/>
			<Button onPress = { this.showTicketList.bind(this) } title = "Cancel" color = "#0e4a80" />
			</View>
		);
	}
}
