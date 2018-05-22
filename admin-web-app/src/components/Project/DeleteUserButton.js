import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

export default class DeleteUserButton extends Component {

	deleteTicket() {
    var url = URL;
		url += '/projects/' + this.props.keyFromParent + '/users/' + this.props.proj.row.loginName;
    fetch(url, {method:'DELETE', headers: getAuth()})
      .then(response => response.json());
	}

	render() {
		return (	// TODO: add edit icon instead of text here
			<Text
				onPress = { this.deleteTicket.bind(this) }
				style={{color: '#5daedb'}}
			>
				DELETE
			</Text>
		);
	}
}
