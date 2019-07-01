resource "aws_vpc" "dev-fredag-vpc" {
  cidr_block = "200.0.0.0/16"

  tags {
    Name = "dev-fredag-vpc"
  }
}
