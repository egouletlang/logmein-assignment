
from util import Client
from test_base import BaseTest

class TestGamesPlayers(BaseTest):

    # GET /games/{game_id}/players

    def test_getGamesPlayers_checkForPresence(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()
        player_2_id = self.create_test_player()

        # Regular get - Test game shouldn't be in there
        status, response = self.client.get('/games/%s/players' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(response != None)

        self.add_player_to_game(game_id, player_1_id)
    
        # Make sure it shows up only once
        status, response = self.client.get('/games/%s/players' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(player_1_id in response)
        
        self.add_player_to_game(game_id, player_2_id)
    
        # Make sure it shows up only once
        status, response = self.client.get('/games/%s/players' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(player_1_id in response)
        self.assertTrue(player_2_id in response)

    def test_getGamesPlayers_invalidGameId(self):
        status, response = self.client.get('/games/%s/players' % self.randomString())
        self.assertEqual(status, 404)

    # POST /games/{game_id}/players

    def test_postPlayers_valid(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        status, response = self.client.post('/games/%s/players' % game_id, {'id' : player_1_id})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        self.assertTrue(player_1_id in response)

    def test_postPlayers_invalidArgs(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        status, response = self.client.post('/games/%s/players' % game_id, {'bad-variable-name': self.randomString()})
        self.assertEqual(status, 400)

    def test_postPlayers_invalidGameId(self):
        player_1_id = self.create_test_player()

        status, response = self.client.post('/games/%s/players' % self.randomString(), {'id' : player_1_id})
        self.assertEqual(status, 404)

    def test_postPlayers_repeatedId(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        status, response = self.client.post('/games/%s/players' % game_id, {'id' : player_1_id})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, list)
        
        count = 0
        for playerId in response:
            if playerId == player_1_id:
                count += 1

        self.assertEqual(count, 1)

    # DELETE /games/{game_id}/players/{player_id}

    def test_deleteGames_valid(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Get by id - valid id
        status, response = self.client.delete('/games/%s/players/%s' % (game_id, player_1_id))
        self.assertEqual(status, 202)

    def test_deleteGames_invalidGameId(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Get by id - valid id
        status, response = self.client.delete('/games/%s/players/%s' % (self.randomString(), player_1_id))
        self.assertEqual(status, 404)

    def test_deleteGames_invalidId(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Get by id - invalid id
        status, response = self.client.delete('/games/%s/players/%s' % (self.randomString(), player_1_id))
        self.assertEqual(status, 404)


    # GET /games/{game_id}/players/{player_id}/hand

    def test_getPlayerHand_valid(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': 5})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 42)

        # Get by id - valid id
        status, response = self.client.get('/games/%s/players/%s/hand' % (game_id, player_1_id))
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response, None)

    def test_getPlayerHand_valid_empty(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': 5})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 42)

        # Get by id - valid id
        status, response = self.client.get('/games/%s/players/%s/hand' % (game_id, player_1_id))
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('names'), None)
        self.assertEqual(len(response.get('names')), 5)

        status, response = self.client.post('/games/%s/deck/collect' % game_id)
        status, response = self.client.get('/games/%s/players/%s/hand' % (game_id, player_1_id))
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('names'), None)
        self.assertEqual(len(response.get('names')), 0)

    def test_getPlayerHand_invalidGameId(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': 5})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 42)

        # Get by id - valid id
        status, response = self.client.get('/games/%s/players/%s/hand' % (self.randomString(), player_1_id))
        self.assertEqual(status, 404)

    def test_getPlayerHand_invalidPlayerId(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': 5})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 42)

        # Get by id - valid id
        status, response = self.client.get('/games/%s/players/%s/hand' % (game_id, self.randomString()))
        self.assertEqual(status, 404)


    # Functional test - Players and Games hold soft references to each other.
    # these tests aim to make sure references are getting cleaned up correctly

    def test_deleteGame(self):

        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Player in group reference
        status, response = self.client.post('/games/%s/players' % game_id, {'id' : player_1_id})
        self.assertTrue(player_1_id in response)

        # Group in player reference
        status, response = self.client.get('/players/%s' % player_1_id)
        self.assertTrue(game_id in response.get('gameIds'))

        # Delete the group
        status, response = self.client.delete('games/%s' % game_id)
        self.assertEqual(status, 202)

        # Group not in player reference
        status, response = self.client.get('/players/%s' % player_1_id)
        self.assertFalse(game_id in response.get('gameIds'))

    def test_deletePlayer(self):

        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Player in group reference
        status, response = self.client.post('/games/%s/players' % game_id, {'id' : player_1_id})
        self.assertTrue(player_1_id in response)

        # Group in player reference
        status, response = self.client.get('/players/%s' % player_1_id)
        self.assertTrue(game_id in response.get('gameIds'))

        # Delete the group
        status, response = self.client.delete('players/%s' % player_1_id)
        self.assertEqual(status, 202)

        # Player not in group reference
        status, response = self.client.get('/games/%s/players' % game_id)
        self.assertFalse(player_1_id in response)

    def test_removePlayer(self):

        game_id = self.create_test_game()
        player_1_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)

        # Player in group reference
        status, response = self.client.post('/games/%s/players' % game_id, {'id' : player_1_id})
        self.assertTrue(player_1_id in response)

        # Group in player reference
        status, response = self.client.get('/players/%s' % player_1_id)
        self.assertTrue(game_id in response.get('gameIds'))

        # Delete the group
        status, response = self.client.delete('/games/%s/players/%s' % (game_id, player_1_id))
        self.assertEqual(status, 202)

        # Group not in player reference
        status, response = self.client.get('/players/%s' % player_1_id)
        self.assertFalse(game_id in response.get('gameIds'))

        # Player not in group reference
        status, response = self.client.get('/games/%s/players' % game_id)
        self.assertFalse(player_1_id in response)



