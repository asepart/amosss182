import React, { Component } from 'react';
import { Link } from 'react-router-dom'
import { View, Text } from 'react-native'
import { setState } from '../shared/GlobalState';

export default class Header extends Component {

    showProjectList () {
		setState({
			isAuth: true,
			show: ''
		});
    }

    render () {

        return (
            // TODO: add home icon instead of text here
            <View>
                <Link to="/" style={{textDecoration: 'none'}}>
                <Text
                    onPress = { this.showProjectList.bind(this) }
                    style={{color: '#5daedb', marginLeft: 5}}
                >
                    HOME
                </Text>
                </Link>
            </View>
        )
    }
}
