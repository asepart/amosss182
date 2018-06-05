import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class TicketChatButton extends Component {

  showTicketChat() {
		setState({
			isAuth: true,
			show: 'ticketChat',
			param: this.props.keyFromParent,
			name: this.props.nameFromParent,
			id: this.props.proj.row.id,
			tName: this.props.proj.row.ticketName
		});
	}

	render() {
		return (	// TODO: add edit icon instead of text here? maybe bubble
			<Link to={ '/projects/' + this.props.keyFromParent + '/tickets/' + this.props.proj.row.id + '/chat'}
				  style={{textDecoration: 'none'}}>
			<Text
				onPress = { this.showTicketChat.bind(this) }
				style={{color: '#5daedb'}}
			>
				CHAT
			</Text>
			</Link>
		);
	}
}
