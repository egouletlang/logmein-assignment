
from util import Client
from test_base import BaseTest

class TestGamesPlayers(BaseTest):

    # GET /games/{game_id}/deck

    def test_getGamesDeck_checkForPresence(self):
        game_id, _, _ = self.setup_game()

        status, response = self.client.get('/games/%s/deck' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)

    def test_getGamesDeck_checkForPresence_invalidGameId(self):
        game_id, _, _ = self.setup_game()

        status, response = self.client.get('/games/%s/deck' % self.randomString())
        self.assertEqual(status, 404)

    # POST /games/{game_id}/deck/add

    def test_getGamesDeck_add(self):
        game_id, _, _ = self.setup_game()

        status, response = self.client.post('/games/%s/deck/add' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 52)

        status, response = self.client.post('/games/%s/deck/add' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 104)

    def test_getGamesDeck_add_invalidGameId(self):
        game_id = self.create_test_game()
        player_1_id = self.create_test_player()
        player_2_id = self.create_test_player()

        self.add_player_to_game(game_id, player_1_id)
        self.add_player_to_game(game_id, player_2_id)

        status, response = self.client.post('/games/%s/deck/add' % self.randomString())
        self.assertEqual(status, 404)

    # POST /games/{game_id}/deck/shuffle

    def test_getGamesDeck_shuffle(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/shuffle' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertNotEqual(response.get('cards')[0], 0)

    def test_getGamesDeck_shuffle_invalidGameId(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/shuffle' % self.randomString())
        self.assertEqual(status, 404)

    # POST /games/{game_id}/deck/deal

    def test_getGamesDeck_deal(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 50) # one to each player

    def test_getGamesDeck_deal_invalidGameId(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % self.randomString())
        self.assertEqual(status, 404)

    def test_getGamesDeck_dealWithValidCount(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': 5})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 42) # 5 to each player

    def test_getGamesDeck_dealWithInvalidCount(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'count': -1})
        self.assertEqual(status, 400)

    def test_getGamesDeck_dealWithValidPlayer(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'id': player_1_id})
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 51) # 1 to each player to player_1_id

    def test_getGamesDeck_dealWithInvalidPlayer(self):
        game_id, player_1_id, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id, {'id': self.randomString()})
        self.assertEqual(status, 400)

    # POST /games/{game_id}/deck/collect

    def test_getGamesDeck_collect(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 50) # one to each player

        status, response = self.client.post('/games/%s/deck/collect' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 52) # one to each player

    def test_getGamesDeck_collect_invalidGameId(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)

        status, response = self.client.post('/games/%s/deck/deal' % game_id)
        self.assertEqual(status, 200)
        self.assertIsInstance(response, dict)
        self.assertNotEqual(response.get('cards'), None)
        self.assertEqual(len(response.get('cards')), 50) # one to each player

        status, response = self.client.post('/games/%s/deck/collect' % self.randomString())
        self.assertEqual(status, 404)

    # GET /games/{game_id}/deck/remaining
    def test_getGamesDeck_remaining(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)
        self.shuffle_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining' % game_id)
        self.assertEqual(len(response), 52)
        for remaining in response:
            self.assertEqual(remaining.get('count'), 1)

        self.add_deck(game_id)
        self.add_deck(game_id)
        self.add_deck(game_id)
        self.add_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining' % game_id)
        self.assertEqual(len(response), 52)
        for remaining in response:
            self.assertEqual(remaining.get('count'), 5)

    def test_getGamesDeck_remaining_invalidGameId(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)
        self.shuffle_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining' % self.randomString())
        self.assertEqual(status, 404)

    # GET /games/{game_id}/deck/remaining/suit
    def test_getGamesDeck_remainingSuit(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)
        self.shuffle_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining/suit' % game_id)
        self.assertEqual(status, 200)
        self.assertEqual(len(response), 4)
        for remaining in response:
            self.assertEqual(remaining.get('count'), 13)

        self.add_deck(game_id)
        self.add_deck(game_id)
        self.add_deck(game_id)
        self.add_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining/suit' % game_id)
        self.assertEqual(status, 200)
        self.assertEqual(len(response), 4)
        for remaining in response:
            self.assertEqual(remaining.get('count'), 65)

    def test_getGamesDeck_remainingSuit_invalidGameId(self):
        game_id, _, _ = self.setup_game()
        self.add_deck(game_id)
        self.shuffle_deck(game_id)

        status, response = self.client.get('/games/%s/deck/remaining/suit' % self.randomString())
        self.assertEqual(status, 404)


