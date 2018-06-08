import React, { Component } from 'react';
import { Link } from 'react-router-dom';

export default class TicketChatButton extends Component {

	render() {
		return (
      <div>
  			<Link to={ '/projects/' + this.props.keyFromParent + '/tickets/' + this.props.proj.row.id + '/chat'}>
            <img style={{height: 25, marginBottom: -5}} src={require('../images/chat.png')} alt=""/>
  			</Link>
      </div>
		);
	}
}
