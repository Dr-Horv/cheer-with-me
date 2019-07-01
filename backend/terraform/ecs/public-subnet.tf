resource "aws_subnet" "dev-fredagPubSN0-0" {
  vpc_id            = "${aws_vpc.dev-fredag-vpc.id}"
  cidr_block        = "200.0.1.0/24"
  availability_zone = "eu-north-1a"

  tags {
    Name = "dev-ecsfredagPubSN0-0-0"
  }
}

resource "aws_subnet" "dev-fredagPubSN0-1" {
  vpc_id            = "${aws_vpc.dev-fredag-vpc.id}"
  cidr_block        = "200.0.2.0/24"
  availability_zone = "eu-north-1b"

  tags {
    Name = "dev-ecsfredagPubSN0-0-1"
  }
}
