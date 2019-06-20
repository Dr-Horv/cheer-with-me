resource "aws_internet_gateway" "dev-fredag-ig" {
  vpc_id = "${aws_vpc.dev-fredag-vpc.id}"

  tags {
    Name = "dev-fredag-ig"
  }
}
