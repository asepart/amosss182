import React, { Component } from 'react';
import { Text } from 'react-native';
import { setState } from '../shared/GlobalState';
import { Link } from 'react-router-dom';

export default class TicketDetailButton extends Component {

  showTicketDetails() {
		setState({
			isAuth: true,
			show: 'ticketDetail',
			param: this.props.keyProj,
			id: this.props.proj.row.id
		});
	}

	render() {
		return (
			<Link to={"/projects/" + this.props.keyProj + "/tickets/" + this.props.proj.row.id } style={{textDecoration: 'none'}}>
				<Text
					onPress = { this.showTicketDetails.bind(this) }
					style={{color: '#5daedb'}}
				>
					{ this.props.proj.row.ticketName }
				</Text>
			</Link>
		);
	}
}
