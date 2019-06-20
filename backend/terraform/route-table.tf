resource "aws_route_table" "dev-fredagPubSN0-0RT" {
  vpc_id = "${aws_vpc.dev-fredag-vpc.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.dev-fredag-ig.id}"
  }

  tags {
    Name = "dev-fredagPubSN0-0RT"
  }
}

resource "aws_route_table_association" "dev-fredagSN0-0RTAssn" {
  subnet_id      = "${aws_subnet.dev-fredagPubSN0-0.id}"
  route_table_id = "${aws_route_table.dev-fredagPubSN0-0RT.id}"
}

resource "aws_route_table" "dev-fredagPubSN0-1RT" {
  vpc_id = "${aws_vpc.dev-fredag-vpc.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.dev-fredag-ig.id}"
  }

  tags {
    Name = "dev-fredagPubSN0-1RT"
  }
}

resource "aws_route_table_association" "dev-fredagSN0-1RTAssn" {
  subnet_id      = "${aws_subnet.dev-fredagPubSN0-1.id}"
  route_table_id = "${aws_route_table.dev-fredagPubSN0-1RT.id}"
}
