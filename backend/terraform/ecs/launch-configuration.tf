resource "aws_launch_configuration" "ecs-launch-configuration" {
  image_id             = "ami-0c788f17fd2f1f650"
  instance_type        = "t3.micro"
  iam_instance_profile = "${aws_iam_instance_profile.ecs-instance-profile.id}"

  root_block_device {
    volume_type           = "standard"
    volume_size           = 30
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  security_groups             = ["${aws_security_group.dev-fredag_public_sg.id}"]
  associate_public_ip_address = "true"
  key_name                    = "${var.ecs_key_pair_name}"

  user_data = <<EOF
                                  #!/bin/bash
                                  echo ECS_CLUSTER=${var.ecs_cluster} >> /etc/ecs/ecs.config
                                  EOF
}
