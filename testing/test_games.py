
from util import Client
from test_base import BaseTest

class TestGames(BaseTest):

    # GET /games

    def test_getGames_checkForPresence(self):
        # Regular get - Test game shouldn't be in there
        status, response = self.client.get('games')
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        for game in response:
            self.assertFalse(game.get('name') == BaseTest.GAME_NAME)

        # Add Test game
        self.create_test_game()

        # Make sure it shows up only once
        status, response = self.client.get('games')
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        count = 0
        for game in response:
            if game.get('name') == BaseTest.GAME_NAME:
                count += 1

        self.assertTrue(count == 1)

    def test_getGames_validName(self):
        self.create_test_game()

        # Get by name - valid name
        status, response = self.client.get('games', {'name': BaseTest.GAME_NAME})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(len(response) > 0)

    def test_getGames_invalidName(self):
        self.create_test_game()

        # Get by name - invalid name
        status, response = self.client.get('games', {'name': BaseTest.GAME_NAME + self.randomString() })
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(len(response) == 0)

    # POST /games 

    def test_postGames_valid(self):
        status, response = self.client.post('games', {'name': BaseTest.GAME_NAME})
        self.assertEqual(status, 201)
        self.assertIsInstance(response, dict)

    def test_postGames_invalidArgs(self):
        status, response = self.client.post('games', {'bad-variable-name': BaseTest.GAME_NAME})
        self.assertEqual(status, 400)

    def test_postGames_repeatedName(self):
        self.create_test_game()

        status, response = self.client.post('games', {'name': BaseTest.GAME_NAME})
        self.assertEqual(status, 409)

    # GET /games/{id}

    def test_getGamesById_valid(self):
        test_game_id = self.create_test_game()

        # Get by id - valid id
        status, response = self.client.get('games/%s' % test_game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertTrue(response.get('id') != None)

    def test_getGamesBydId_invalidId(self):
        test_game_id = self.create_test_game()

        # Get by id - invalid id
        status, response = self.client.get('games/%s' % (test_game_id + self.randomString()))
        self.assertEqual(status, 404)

    # DELETE /games/{id}

    def test_deleteGamesById_valid(self):
        test_game_id = self.create_test_game()

        # Get by id - valid id
        status, response = self.client.delete('games/%s' % test_game_id)
        self.assertEqual(status, 202)

    def test_deleteGamesById_invalidId(self):
        test_game_id = self.create_test_game()

        # Get by id - invalid id
        status, response = self.client.get('games/%s' % (test_game_id + self.randomString()))
        self.assertEqual(status, 404)



