import React, { Component } from 'react';
import { View, Button } from 'react-native';
import Popup from "reactjs-popup";
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';

export default class DeleteTicketButton extends Component {

  constructor(props) {
    super(props);
    this.state = { open: false };
  }
  openPopup = () => {
    this.setState({ open: true });
  };
  closePopup = () => {
    this.setState({ open: false });
  };

  deleteTicket() {
    var url = URL;
		url += '/projects/' + this.props.keyFromParent + '/tickets/' + this.props.proj.row.id;
    fetch(url, {method:'DELETE', headers: getAuth()})
      .then((response) => response.json())
      .catch((error) => {
        console.error(error);
      });
    this.setState({
        open: false
  	})
	}

	render() {
		return (	// TODO: add edit icon instead of text here
      <div>
        <button onClick={this.openPopup} style={{color: '#5daedb'}}>
          DELETE
        </button>
        <Popup
          open={this.state.open}
          closeOnDocumentClick
          onClose={this.closePopup}
        >
          <View>
          <Button
            onPress = { function doNothing() {} }
            disabled = {true}
            title = {"Delete " + this.props.proj.row.ticketName + " from " + this.props.nameFromParent + "?"}
          />
          <Button onPress = { this.deleteTicket.bind(this) } title = "Delete" color = "#0c3868"/>
          <Button onPress = { this.closePopup } title = "Cancel" color = "#0e4a80" />
          </View>
        </Popup>
      </div>
		);
	}
}
