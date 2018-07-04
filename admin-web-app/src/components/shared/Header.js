import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { View } from 'react-native';
import { logout } from '../Login/logout.js';

export default class Header extends Component {

    //TODO add logout button code

    render () {
        return (
          <View style={{flexDirection: 'row', marginBottom: -3}}>
            <div style={{flex: 1}}>
    					<Link to = "/" style={{textDecoration: 'none'}} >
    					<img style={{height: 30, width: undefined}} src={require('../images/icon.png')} alt=""/>
    					</Link>
    		</div>
            <div style={{flex: 1, direction: 'rtl'}}>
            			<Link to = "/" style={{textDecoration: 'none'}} >
              			<img style={{height: 30, width: undefined}} src={require('../images/logout.png')} alt="" onClick={logout}/>
              			</Link>
            </div>
          </View>
        )
    }
}
