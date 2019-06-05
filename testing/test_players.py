
from util import Client
from test_base import BaseTest

class TestPlayers(BaseTest):

    # GET /players

    def test_getPlayers_checkForPresence(self):
        # Regular get - Test game shouldn't be in there
        status, response = self.client.get('players')
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        for player in response:
            self.assertFalse(BaseTest.PLAYER_USERNAME in player.get('username'))

        # Add A Player
        self.create_test_player()

        # Make sure it shows up only once
        status, response = self.client.get('players')
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        count = 0
        for player in response:
            if BaseTest.PLAYER_USERNAME in player.get('username'):
                count += 1

        self.assertTrue(count == 1)

        # Add A Player
        self.create_test_player()

        status, response = self.client.get('players')
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        count = 0
        for player in response:
            if BaseTest.PLAYER_USERNAME in player.get('username'):
                count += 1

        self.assertTrue(count == 2)

    def test_getPlayers_validUsername(self):
        self.create_test_player()

        # Get by name - valid name
        status, response = self.client.get('players', {'username': BaseTest.PLAYER_USERNAME + str(self.player_count - 1)})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertTrue(response != None)

    def test_getPlayers_invalidUsername(self):
        # Get by name - invalid name
        status, response = self.client.get('players', {'username': BaseTest.PLAYER_USERNAME + self.randomString() })
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(len(response) == 0)

    # POST /players

    def test_postPlayers_valid(self):
        username = BaseTest.PLAYER_USERNAME + str(self.player_count)
        status, response = self.client.post('players', {'username': username, 'firstName': 'TestPlayer', 'lastName': self.player_count})
        self.player_count += 1
        self.assertEqual(status, 201)
        self.assertIsInstance(response, dict)

    def test_postPlayers_invalidArgs(self):
        status, response = self.client.post('players', {'bad-variable-name': self.randomString()})
        self.assertEqual(status, 400)

    def test_postPlayers_repeatedName(self):
        self.create_test_player()

        username = BaseTest.PLAYER_USERNAME + str(self.player_count - 1)
        status, response = self.client.post('players', {'username': username})
        self.assertEqual(status, 409)

    # GET /players/{id}

    def test_getPlayersById_valid(self):
        test_player_id = self.create_test_player()

        # Get by id - valid id
        status, response = self.client.get('players/%s' % test_player_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertTrue(response != None)

    def test_getPlayersById_invalidId(self):
        test_player_id = self.create_test_player()

        # Get by id - invalid id
        status, response = self.client.get('players/%s' % (test_player_id + self.randomString()))
        self.assertEqual(status, 404)

    # DELETE /players/{id}

    def test_deletePlayersById_valid(self):
        test_player_id = self.create_test_player()

        # Get by id - valid id
        status, response = self.client.delete('players/%s' % test_player_id)
        self.assertEqual(status, 202)

    def test_deletePlayersById_invalidId(self):
        test_player_id = self.create_test_player()

        # Get by id - invalid id
        status, response = self.client.delete('players/%s' % (test_player_id + self.randomString()))
        self.assertEqual(status, 404)



