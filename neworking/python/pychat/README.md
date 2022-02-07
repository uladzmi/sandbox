# PyChat

An example of some very basic socket and threading operations.

Comments:
- Peer-to-Peer connection only.
- Message consuming in separate thread.
- `input()` call is blocking and not aware if connection still alive or closed by the client.

Ideas:
- Proper connection management.
- Automatic server discovery within leaving with broadcasting.
- Multiple clients.
