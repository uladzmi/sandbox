#!/usr/bin/env python3

import argparse
import socket
import threading


CLOSE_CONNECTION_MESSAGE = "/close"


def parse_arguments():

    parser = argparse.ArgumentParser()
    parser.add_argument("--host", default="127.0.0.1", help="host to connect")
    parser.add_argument("--port", default=12345, help="port to connect")
    parser.add_argument("--server", action="store_true", default=False, help="run as a server")

    return parser.parse_args()


def handle_incoming_message(connection: socket.socket) -> None:

    while True:
        data = connection.recv(1024)
        if not data:
            break
        message = data.decode()
        if message == CLOSE_CONNECTION_MESSAGE:
            connection.close()
            break
        else:
            print(f"{connection.getpeername()}: {message}")


def main():

    args = parse_arguments()

    server_address = args.host, args.port

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        if args.server:
            s.bind(server_address)
            s.listen()
            #: new socket on connection
            connection, _ = s.accept()
            print(f"{connection.getpeername()}: joined the chat ...")
        else:
            s.connect(server_address)
            connection = s

        consumer = threading.Thread(
            target=handle_incoming_message,
            name="consumer",
            args=(connection,),
            daemon=True
        )
        consumer.start()

        while True:
            try:
                #: blocking call
                #: will not exit until server side types something in
                message = input().encode()
                if connection.fileno() == -1:
                    print(f"Peer left, leaving too ...")
                    exit()
                else:
                    connection.sendall(message)
            except (KeyboardInterrupt, EOFError):
                connection.sendall("/close".encode())
                exit()


if __name__ == "__main__":
    main()
