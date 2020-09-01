#include <linux/module.h>               /* For module specific items */    
#include <linux/moduleparam.h>          /* For new moduleparam's */    
#include <linux/types.h>                /* For standard types (like size_t) */    
#include <linux/errno.h>                /* For the -ENODEV/... values */    
#include <linux/kernel.h>               /* For printk/panic/... */    
#include <linux/fs.h>                   /* For file operations */    
#include <linux/ioport.h>               /* For io-port access */    
#include <linux/platform_device.h>      /* For platform_driver framework */    
#include <linux/init.h>                 /* For __init/__exit/... */    
#include <linux/uaccess.h>              /* For copy_to_user/put_user/... */    
#include <linux/io.h>                   /* For inb/outb/... */    
#include <linux/gpio.h>    
#include <linux/device.h>    
#include <linux/cdev.h>    
#include <linux/slab.h>               /*kamlloc */    
#include <asm-generic/ioctl.h>    

 //ioctl   
#define CMD_FLAG  'i'    
#define gpio_PWR_ON      _IOR(CMD_FLAG,0x00000001,__u32)      
#define gpio_PWR_OFF     _IOR(CMD_FLAG,0x00000000,__u32)   
#define gpio_lp         68  

static int  major =0;    
static struct class *gpio_class;    
struct cdev_gpio {    
    struct cdev cdev;    
};     
struct cdev_gpio *gpio_dev;    

static long gpio_ioctl(struct file* filp, unsigned int cmd,unsigned long argv)    
{    
    printk(KERN_INFO "entry kernel.... \n");    
    //printk(KERN_INFO "%d\n", gpio_PWR_ON);  
    //void __iomem *ldo_mmio_base = ioremap(0xe46002e4, 4);

    switch(cmd)    
    {    
        case gpio_PWR_ON:    
        {    
            gpio_set_value(gpio_lp,1);  //   
            printk(KERN_INFO "gpio on\n");   
            //iowrite32(0x1700, ldo_mmio_base)  
            break;    
        }    
        case gpio_PWR_OFF:    
        {    
            gpio_set_value(gpio_lp,0);  
            printk(KERN_INFO "gpio off \n");  
            //iowrite32(0x1500, ldo_mmio_base);  
            break;    
        }    
        default:    
            return -EINVAL;    
    }    
    return 0;    
}    


//open    
static int gpio_open(struct inode* i_node,struct file* filp)    
{    
    //printk(KERN_INFO "open init.... \n");    
    int err = gpio_request(gpio_lp,"gpio_pwr");  
    if(err<0)    
    {    
        printk(KERN_INFO "gpio request faile \n");    
        return err;    
    }    
    gpio_direction_output(gpio_lp,1);  
    return 0;    
}    

//close    
static int  gpio_close(struct inode* i_node,struct file* filp)    
{    
    printk(KERN_INFO "close init \n"); 
    gpio_free(gpio_lp);   
    return 0;    
}    

/* file operations */    
struct file_operations fops={    
    .owner  = THIS_MODULE,    
    .open   = gpio_open,    
    .unlocked_ioctl = gpio_ioctl,   
    .release = gpio_close,    
};    

static int __init gpio_init(void)    
{    
    //printk(KERN_INFO "init .... \n");    
    dev_t dev_no;    
    int result,err;    
    err = alloc_chrdev_region(&dev_no,0,1,"my_gpio"); //dynamic request device number    
    if(err<0)    
    {    
        printk(KERN_INFO "ERROR\n");    
        return err;    
    }    
    major = MAJOR(dev_no);    
    gpio_dev = kmalloc(sizeof(struct cdev_gpio),GFP_KERNEL);    
    if(!gpio_dev)    
    {    
        result = -ENOMEM;    
        goto fail_malloc;    
    }    
    memset(gpio_dev,0,sizeof(*gpio_dev));    

    cdev_init(&gpio_dev->cdev,&fops);     
    gpio_dev->cdev.owner = THIS_MODULE;    
    result = cdev_add(&gpio_dev->cdev,dev_no,1);     
    if(result <0)    
    {   printk(KERN_INFO "error\n");    
        goto fail_add;    
    }    
    gpio_class = class_create(THIS_MODULE,"mtgpio68");  //in sys/class create sysfs file    
    device_create(gpio_class,NULL,MKDEV(major,0),NULL,"gpio68"); //dynamic create device file  /dev/mygpio    
    return 0;    
fail_add:    
    kfree(gpio_dev);    
fail_malloc:    
    unregister_chrdev_region(dev_no,1);    
    return result;    

}    

static void __exit gpio_exit(void)    
{    
    dev_t dev_no=MKDEV(major,0);    

    unregister_chrdev_region(dev_no,1);    
    cdev_del(&gpio_dev->cdev);    
    kfree(gpio_dev);    
    device_destroy(gpio_class,dev_no);    
    class_destroy(gpio_class);    
    printk(KERN_INFO "exit........ \n");    
}    
module_init(gpio_init);    
module_exit(gpio_exit);    
MODULE_AUTHOR("");    
MODULE_DESCRIPTION("control_gpio68");    
MODULE_LICENSE("GPL");   
