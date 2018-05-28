import React from 'react'
import { Link } from 'react-router-dom'
import { View, Text } from 'react-native'

const Header = () => (
    // TODO: add home icon instead of text here
    <View>
        <Link to="/" style={{textDecoration: 'none'}}>
        <Text
            onPress = { function doNothing() {} }
            style={{color: '#5daedb'}}
        >
            HOME
        </Text>
        </Link>
    </View>
)

export default Header