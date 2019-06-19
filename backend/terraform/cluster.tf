resource "aws_ecs_cluster" "dev-fredag-ecs-cluster" {
  name = "${var.ecs_cluster}"
}
