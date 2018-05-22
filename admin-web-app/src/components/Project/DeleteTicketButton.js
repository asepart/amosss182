import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

export default class DeleteTicketButton extends Component {

  showDeleteTicketConfirm() {
		setState({
			isAuth: true,
			show: 'deleteTicket',
			param: this.props.keyFromParent,
			name: this.props.nameFromParent,
      id: this.props.proj.row.id
		});
	}

	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.showDeleteTicketConfirm.bind(this) }
				style={{color: '#5daedb'}}
			>
				DELETE
			</Text>
		);
	}
}
